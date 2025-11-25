//
//  Crypto.swift
//  iosApp
//
//  Created by mac on 25.11.2025.
//

import Foundation
import CryptoKit
import Security

@objc public class IosEncryptor: NSObject {
    
    private static let keyTag = "aeskey"
    
    private static var key: SymmetricKey = {
        if let existingKey = loadKey() {
            return existingKey
        }
        let newKey = SymmetricKey(size: .bits256)
        saveKey(newKey)
        return newKey
    }()
    
    // Encrypt

    @objc public static func encrypt(_ plainText: String) -> String {
        let data = Data(plainText.utf8)
        do {
            let sealedBox = try AES.GCM.seal(data, using: key)
            guard let combined = sealedBox.combined else {
                print("Encryption failed: no combined value")
                return ""
            }
            return combined.base64EncodedString()
        } catch {
            print("Encryption failed: \(error)")
            return ""
        }
    }
    
    // Decrypt

    @objc public static func decrypt(_ encryptedString: String) -> String {
        do {
            guard let encryptedData = Data(base64Encoded: encryptedString) else {
                print("Decryption failed: invalid base64 string")
                return ""
            }
            
            let box = try AES.GCM.SealedBox(combined: encryptedData)
            let decryptedData = try AES.GCM.open(box, using: key)
            return String(decoding: decryptedData, as: UTF8.self)
        } catch {
            print("Decryption failed: \(error)")
            return ""
        }
    }
    // Keychain Helpers
    
    private static func saveKey(_ key: SymmetricKey) {
        let tag = keyTag.data(using: .utf8)!
        let keyData = key.withUnsafeBytes { Data($0) }
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: "aes-key-service",
            kSecAttrAccount as String: tag,
            kSecValueData as String: keyData,
            kSecAttrAccessible as String: kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        ]
        
        SecItemDelete(query as CFDictionary)
        let status = SecItemAdd(query as CFDictionary, nil) // writing the key into keychain database.
        if status != errSecSuccess {
            print("Failed to store key in Keychain: \(status)")
        }
    }
    
    private static func loadKey() -> SymmetricKey? {
        let tag = keyTag.data(using: .utf8)!
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: "aes-key-service",
            kSecAttrAccount as String: tag,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        if status == errSecSuccess, let data = result as? Data {
            return SymmetricKey(data: data)
        } else {
            print("Key not found in Keychain: \(status)")
            return nil
        }
    }
}

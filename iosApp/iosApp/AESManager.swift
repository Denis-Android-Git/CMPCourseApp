//
//  NSString+AES.swift
//  iosApp
//
//  Created by mac on 21.11.2025.
//

import Foundation
import CommonCrypto
import Foundation
import CommonCrypto

final class AESManager {

    static let shared = AESManager()
    
    private let key: Data
    private let keyLength = kCCKeySizeAES256
    private let blockSize = kCCBlockSizeAES128
    
    private init() {
        var keyBytes = [UInt8](repeating: 0, count: keyLength)
        _ = SecRandomCopyBytes(kSecRandomDefault, keyLength, &keyBytes)
        self.key = Data(keyBytes)
    }
    
    func encrypt(_ string: String) -> String {
        guard let dataToEncrypt = string.data(using: .utf8) else { return "" }
        
        let iv = Data((0..<blockSize).map { _ in UInt8.random(in: 0...255) })
        var encryptedData = Data(count: dataToEncrypt.count + blockSize)
        var numBytesEncrypted: size_t = 0
        
        let count = encryptedData.count
        
        let cryptStatus = encryptedData.withUnsafeMutableBytes { encBytes in
            dataToEncrypt.withUnsafeBytes { dataBytes in
                key.withUnsafeBytes { keyBytes in
                    iv.withUnsafeBytes { ivBytes in
                        CCCrypt(
                            CCOperation(kCCEncrypt),
                            CCAlgorithm(kCCAlgorithmAES),
                            CCOptions(kCCOptionPKCS7Padding),
                            keyBytes.baseAddress!,
                            keyLength,
                            ivBytes.baseAddress!,
                            dataBytes.baseAddress!,
                            dataToEncrypt.count,
                            encBytes.baseAddress!,
                            count,
                            &numBytesEncrypted
                        )
                    }
                }
            }
        }
        
        guard cryptStatus == kCCSuccess else { return "" }
        encryptedData.removeSubrange(numBytesEncrypted..<encryptedData.count)
        let result = iv + encryptedData
        return result.base64EncodedString()
    }
    
    func decrypt(_ base64: String) -> String {
        guard let fullData = Data(base64Encoded: base64), fullData.count > blockSize else { return "" }
        
        let iv = fullData.prefix(blockSize)
        let encryptedData = fullData.suffix(from: blockSize)
        var decryptedData = Data(count: encryptedData.count + blockSize)
        var numBytesDecrypted: size_t = 0
        
        let count = decryptedData.count
        
        let cryptStatus = decryptedData.withUnsafeMutableBytes { decBytes in
            encryptedData.withUnsafeBytes { encBytes in
                key.withUnsafeBytes { keyBytes in
                    iv.withUnsafeBytes { ivBytes in
                        CCCrypt(
                            CCOperation(kCCDecrypt),
                            CCAlgorithm(kCCAlgorithmAES),
                            CCOptions(kCCOptionPKCS7Padding),
                            keyBytes.baseAddress!,
                            keyLength,
                            ivBytes.baseAddress!,
                            encBytes.baseAddress!,
                            encryptedData.count,
                            decBytes.baseAddress!,
                            count,
                            &numBytesDecrypted
                        )
                    }
                }
            }
        }
        
        guard cryptStatus == kCCSuccess else { return "" }
        decryptedData.removeSubrange(numBytesDecrypted..<decryptedData.count)
        return String(data: decryptedData, encoding: .utf8) ?? ""
    }
}



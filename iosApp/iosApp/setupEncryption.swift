//
//  setupEncryption.swift
//  iosApp
//
//  Created by mac on 21.11.2025.
//

import Foundation
import ComposeApp

func setupEncryption() {
    let aes = AES()
    EncryptionHandler().encrypt { data in
        return aes.encrypt(data)
    }

    EncryptionHandler().decrypt { encryptedData in
        return aes.decrypt(encryptedData)
    }
}

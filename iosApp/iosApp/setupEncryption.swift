//
//  setupEncryption.swift
//  iosApp
//
//  Created by mac on 21.11.2025.
//

import Foundation
import ComposeApp

func setupEncryption() {
    EncryptionHandler().encrypt { data in
        return IosEncryptor.encrypt(data)
    }

    EncryptionHandler().decrypt { encryptedData in
        return IosEncryptor.decrypt(encryptedData)
    }
}

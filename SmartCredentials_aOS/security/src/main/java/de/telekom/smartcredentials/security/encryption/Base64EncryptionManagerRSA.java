/*
 * Copyright (c) 2019 Telekom Deutschland AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.telekom.smartcredentials.security.encryption;

import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.NoSuchPaddingException;

import de.telekom.smartcredentials.core.exceptions.EncryptionException;
import de.telekom.smartcredentials.core.security.KeyStoreManagerException;

import static de.telekom.smartcredentials.core.security.EncryptionError.DECRYPTION_EXCEPTION_TEXT;
import static de.telekom.smartcredentials.core.security.EncryptionError.ENCRYPTION_EXCEPTION_TEXT;
import static de.telekom.smartcredentials.core.security.EncryptionError.PUBLIC_KEY_EXCEPTION_TEXT;
import static de.telekom.smartcredentials.security.keystore.SmartCredentialsKeyStoreKeyProvider.KEY_SIZE;

public class Base64EncryptionManagerRSA implements EncryptionManager {

    static final int RSA_KEY_LENGTH = KEY_SIZE;

    private static final int PADDING_OVERHEAD_BYTES = 11;

    static final int MAX_BYTES_LENGTH_TO_ENCRYPT = RSA_KEY_LENGTH - PADDING_OVERHEAD_BYTES;

    private static final String BASE4_CHAR_SET = "UTF-8";

    private final RSACipherManager mRSACipherManager;

    public Base64EncryptionManagerRSA(RSACipherManager rsaCipherManager) {
        mRSACipherManager = rsaCipherManager;
    }

    @Override
    public String encrypt(String toEncrypt, String metaAlias) throws EncryptionException {
        if (TextUtils.isEmpty(toEncrypt)) {
            return toEncrypt;
        }

        try {
            SmartCredentialsCipherWrapper smartCredentialsCipherWrapper = mRSACipherManager.getEncryptionCipherWrapper(metaAlias);

            byte[] toEncryptBytes = toEncrypt.getBytes(BASE4_CHAR_SET);
            byte[] encryptedBytes = mRSACipherManager.getMultiBlockBytes(toEncryptBytes, smartCredentialsCipherWrapper, MAX_BYTES_LENGTH_TO_ENCRYPT);

            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
                | KeyStoreManagerException e) {
            throw new EncryptionException(ENCRYPTION_EXCEPTION_TEXT + e.getMessage(), e);
        }
    }

    @Override
    public String decrypt(String encryptedText, String metaAlias) throws EncryptionException {
        if (TextUtils.isEmpty(encryptedText)) {
            return encryptedText;
        }

        try {
            SmartCredentialsCipherWrapper smartCredentialsCipherWrapper = mRSACipherManager.getDecryptionCipherWrapper(metaAlias);

            byte[] encryptedTextBytes = Base64.decode(encryptedText, Base64.DEFAULT);
            byte[] decryptedBytes = mRSACipherManager.getMultiBlockBytes(encryptedTextBytes, smartCredentialsCipherWrapper, RSA_KEY_LENGTH);

            return new String(decryptedBytes, 0, decryptedBytes.length, BASE4_CHAR_SET);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | KeyStoreManagerException | InvalidKeyException e) {
            throw new EncryptionException(DECRYPTION_EXCEPTION_TEXT + e.getMessage(), e);
        }
    }

    public PublicKey getPublicKey(String alias) throws EncryptionException {
        try {
            return mRSACipherManager.getKeyStorePrivateEntryPublicKey(alias);
        } catch (KeyStoreManagerException e) {
            throw new EncryptionException(PUBLIC_KEY_EXCEPTION_TEXT + e.getMessage(), e);
        }
    }
}

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

/*
 * Created by Lucian Iacob on 7/2/18 10:34 AM.
 * lucian.iacob@endava.com
 * Deutsche Telekom - All Rights Reserved - ©
 */


package de.telekom.smartcredentials.core.model;

import java.io.Serializable;

public abstract class DocumentScannerResult implements Serializable {

    private final DocumentScannerResultState mDocumentScannerResultState;

    protected DocumentScannerResult(DocumentScannerResultState scannerResultState) {
        this.mDocumentScannerResultState = scannerResultState;
    }

    public DocumentScannerResultState getDocumentScannerResultState() {
        return mDocumentScannerResultState;
    }
}

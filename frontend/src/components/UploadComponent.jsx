import React, { useRef } from 'react';

function UploadComponent({ onUpload, documents, onClear }) {
    const fileInputRef = useRef(null);

    const handleFileChange = (e) => {
        if (e.target.files.length > 0) {
            onUpload(Array.from(e.target.files));
            e.target.value = '';
        }
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        e.stopPropagation();
    };

    const handleDrop = (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.dataTransfer.files.length > 0) {
            onUpload(Array.from(e.dataTransfer.files));
        }
    };

    return (
        <div className="upload-component">
            <h2>Document Upload</h2>
            <div
                className="dropzone"
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current.click()}
            >
                <p>Drag & drop files here or click to select</p>
                <input
                    type="file"
                    ref={fileInputRef}
                    onChange={handleFileChange}
                    multiple
                    style={{ display: 'none' }}
                />
            </div>
            {documents.length > 0 && (
                <div className="document-list">
                    <h3>Uploaded Documents ({documents.length})</h3>
                    <ul>
                        {documents.map((doc, index) => (
                            <li key={index}>
                                {doc.name} ({(doc.size / 1024).toFixed(2)} KB)
                            </li>
                        ))}
                    </ul>
                    <button onClick={onClear} className="clear-documents-button">
                        Clear Documents
                    </button>
                </div>
            )}
        </div>
    );
}

export default UploadComponent;

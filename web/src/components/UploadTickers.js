import React from 'react';

const UploadTickers = () => {
    const handleFileUpload = (event) => {
        // handle file upload logic
    };

    return (
        <div>
            <input type="file" onChange={handleFileUpload} />
        </div>
    );
};

export default UploadTickers;

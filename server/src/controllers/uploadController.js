const uploadAudio = (req, res) => {
    if (!req.file) {
        return res.status(400).json({
            message: "No audio uploaded"
        });
    }

    const url = `http://localhost:5000/uploads/audio/${req.file.filename}`;

    res.json({
        url
    });
};

const uploadImage = (req, res) => {
    if (!req.file) {
        return res.status(400).json({
            message: "No image uploaded"
        });
    }

    const url = `http://localhost:5000/uploads/images/${req.file.filename}`;

    res.json({
        url
    });
};

module.exports = {
    uploadAudio,
    uploadImage
};
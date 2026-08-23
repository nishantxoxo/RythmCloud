const uploadAudio = (req, res) => {
    if (!req.file) {
        return res.status(400).json({
            message: "No audio uploaded"
        });
    }

    const url = `${process.env.SERVER_URL}/uploads/audio/${req.file.filename}`;

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

    const url = `${process.env.SERVER_URL}/uploads/images/${req.file.filename}`;

    res.json({
        url
    });
};

module.exports = {
    uploadAudio,
    uploadImage
};
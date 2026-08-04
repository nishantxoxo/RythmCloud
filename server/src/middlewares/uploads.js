const multer = require("multer");
const path = require("path");

// Audio storage
const audioStorage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, "uploads/audio");
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});

// Image storage
const imageStorage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, "uploads/images");
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});

const uploadAudio = multer({
    storage: audioStorage
});

const uploadImage = multer({
    storage: imageStorage
});

module.exports = {
    uploadAudio,
    uploadImage
};
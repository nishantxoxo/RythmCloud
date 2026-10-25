const multer = require("multer");

const allowedAudioTypes = new Set([
    "audio/mpeg",
    "audio/mp4",
    "audio/wav",
    "audio/x-wav",
    "audio/ogg"
]);

const allowedImageTypes = new Set([
    "image/jpeg",
    "image/png",
    "image/webp"
]);

const uploadAudio = multer({
    storage: multer.memoryStorage(),
    limits: {
        fileSize: 50 * 1024 * 1024
    },
    fileFilter: (req, file, callback) => {
        if (!allowedAudioTypes.has(file.mimetype)) {
            return callback(new Error("Unsupported audio file type"));
        }

        callback(null, true);
    }
});

const uploadImage = multer({
    storage: multer.memoryStorage(),
    limits: {
        fileSize: 10 * 1024 * 1024
    },
    fileFilter: (req, file, callback) => {
        if (!allowedImageTypes.has(file.mimetype)) {
            return callback(new Error("Unsupported image file type"));
        }

        callback(null, true);
    }
});

module.exports = {
    uploadAudio,
    uploadImage
};
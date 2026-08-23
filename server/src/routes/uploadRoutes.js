const express = require("express");

const router = express.Router();

const {
    uploadAudio,
    uploadImage
} = require("../controllers/uploadController");

const {
    uploadAudio: audioMiddleware,
    uploadImage: imageMiddleware
} = require("../middlewares/uploads");

router.post(
    "/audio",
    audioMiddleware.single("audio"),
    uploadAudio
);

router.post(
    "/image",
    imageMiddleware.single("image"),
    uploadImage
);

module.exports = router;
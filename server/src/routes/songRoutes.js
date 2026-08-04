const express = require("express");

const router = express.Router();

const {
    createSong,
    getAllSongs
} = require("../controllers/songController");

router.post("/", createSong);

router.get("/", getAllSongs);

module.exports = router;
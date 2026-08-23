const express = require("express");

const router = express.Router();

const {
    createSong,
    getAllSongs,
    deleteAllSongs
} = require("../controllers/songController");

router.post("/", createSong);

router.get("/", getAllSongs);

router.delete("/", deleteAllSongs);

module.exports = router;
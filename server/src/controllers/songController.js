const songService = require("../services/songService");

const createSong = async (req, res) => {
    try {
        const song = await songService.createSong(req.body);

        res.status(201).json(song);
    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: "Failed to create song"
        });
    }
};

const getAllSongs = async (req, res) => {
    try {
        const songs = await songService.getAllSongs();

        res.json(songs);
    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: "Failed to fetch songs"
        });
    }
};

module.exports = {
    createSong,
    getAllSongs
};
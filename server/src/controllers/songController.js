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
        const query = typeof req.query.query === "string"
            ? req.query.query.trim()
            : "";
        const songs = await songService.getAllSongs(query);

        res.json(songs);
    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: "Failed to fetch songs"
        });
    }
};

const deleteAllSongs = async (req, res) => {
    try {
        const result = await songService.deleteAllSongs();

        res.json({
            message: "All songs deleted successfully",
            deletedCount: result.count
        });
    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: "Failed to delete songs"
        });
    }
};

module.exports = {
    createSong,
    getAllSongs,
    deleteAllSongs
};
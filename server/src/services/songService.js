const prisma = require("../config/db");

const createSong = async (songData) => {
    return await prisma.song.create({
        data: songData
    });
};

const getAllSongs = async () => {
    return await prisma.song.findMany({
        orderBy: {
            id: "asc"
        }
    });
};

const deleteAllSongs = async () => {
    return await prisma.song.deleteMany({});
};

module.exports = {
    createSong,
    getAllSongs,
    deleteAllSongs
};
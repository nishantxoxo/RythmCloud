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

module.exports = {
    createSong,
    getAllSongs
};
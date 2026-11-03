const prisma = require("../config/db");

const createSong = async (songData) => {
    return await prisma.song.create({
        data: songData
    });
};

const getAllSongs = async (query = "") => {
    return await prisma.song.findMany({
        ...(query
            ? {
                where: {
                    OR: [
                        { title: { contains: query, mode: "insensitive" } },
                        { subtitle: { contains: query, mode: "insensitive" } }
                    ]
                }
            }
            : {}),
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
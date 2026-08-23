const app = require("./app");
const prisma = require("./config/db");

const PORT = 5000;

async function startServer() {
    try {
        await prisma.$connect();
        console.log("Database connected");

        app.listen(PORT, () => {
            console.log(`Server running on port ${PORT}`);
        });
    } catch (error) {
        console.error("Database connection failed:", error.message);
        process.exitCode = 1;
    }
}

startServer();
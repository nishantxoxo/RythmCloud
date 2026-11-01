const jwt = require("jsonwebtoken");
const prisma = require("../config/db");
const { getJwtSecret } = require("../services/authService");

const requireAuth = async (req, res, next) => {
    const authorization = req.headers.authorization;

    if (!authorization || !authorization.startsWith("Bearer ")) {
        return res.status(401).json({
            message: "Authentication required"
        });
    }

    const token = authorization.slice("Bearer ".length).trim();

    if (!token) {
        return res.status(401).json({
            message: "Authentication required"
        });
    }

    try {
        const payload = jwt.verify(token, getJwtSecret());

        if (typeof payload !== "object" || !payload.sub || typeof payload.sub !== "string") {
            return res.status(401).json({
                message: "Invalid authentication token"
            });
        }

        const user = await prisma.user.findUnique({
            where: {
                id: payload.sub
            }
        });

        if (!user) {
            return res.status(401).json({
                message: "User no longer exists"
            });
        }

        req.user = user;
        next();
    } catch (error) {
        if (error.name === "TokenExpiredError") {
            return res.status(401).json({
                message: "Authentication token expired"
            });
        }

        if (error.name === "JsonWebTokenError" || error.name === "NotBeforeError") {
            return res.status(401).json({
                message: "Invalid authentication token"
            });
        }

        console.error("Authentication middleware failed:", error);
        return res.status(500).json({
            message: "Authentication failed"
        });
    }
};

module.exports = {
    requireAuth
};

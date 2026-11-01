const jwt = require("jsonwebtoken");

const getJwtSecret = () => {
    if (!process.env.JWT_SECRET) {
        throw new Error("JWT_SECRET is not configured");
    }

    return process.env.JWT_SECRET;
};

const createAccessToken = (user) => {
    return jwt.sign(
        {
            sub: user.id,
            role: user.role
        },
        getJwtSecret(),
        {
            expiresIn: process.env.JWT_EXPIRES_IN || "15m"
        }
    );
};

const toPublicUser = (user) => ({
    id: user.id,
    email: user.email,
    displayName: user.displayName,
    role: user.role
});

module.exports = {
    createAccessToken,
    getJwtSecret,
    toPublicUser
};

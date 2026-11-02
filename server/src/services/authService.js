const jwt = require("jsonwebtoken");
const crypto = require("crypto");

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

const createRefreshTokenValue = () => crypto.randomBytes(64).toString("base64url");

const hashRefreshToken = (token) => {
    return crypto.createHash("sha256").update(token).digest("hex");
};

const getRefreshTokenExpiry = () => {
    const expiresIn = process.env.JWT_REFRESH_EXPIRES_IN || "30d";
    const match = /^(\d+)([smhd])$/.exec(expiresIn);

    if (!match) {
        throw new Error("JWT_REFRESH_EXPIRES_IN must use s, m, h, or d");
    }

    const multipliers = { s: 1000, m: 60000, h: 3600000, d: 86400000 };
    return new Date(Date.now() + Number(match[1]) * multipliers[match[2]]);
};

const createRefreshToken = async (prisma, userId) => {
    const value = createRefreshTokenValue();
    await prisma.refreshToken.create({
        data: {
            tokenHash: hashRefreshToken(value),
            userId,
            expiresAt: getRefreshTokenExpiry()
        }
    });
    return value;
};

module.exports = {
    createAccessToken,
    getJwtSecret,
    toPublicUser,
    createRefreshToken,
    hashRefreshToken
};

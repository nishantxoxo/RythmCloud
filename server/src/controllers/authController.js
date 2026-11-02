const bcrypt = require("bcrypt");
const prisma = require("../config/db");
const {
    createAccessToken,
    toPublicUser,
    createRefreshToken,
    hashRefreshToken
} = require("../services/authService");

const normalizeEmail = (email) => email.trim().toLowerCase();

const validateCredentials = ({ email, password }) => {
    if (typeof email !== "string" || !email.trim()) {
        return "Email is required";
    }

    if (typeof password !== "string" || password.length < 8) {
        return "Password must be at least 8 characters";
    }

    return null;
};

const register = async (req, res) => {
    const { email, password, displayName } = req.body;

    if (typeof displayName !== "undefined" && displayName !== null && typeof displayName !== "string") {
        return res.status(400).json({
            message: "Display name must be a string"
        });
    }

    const validationError = validateCredentials({ email, password });

    if (validationError) {
        return res.status(400).json({
            message: validationError
        });
    }

    try {
        const normalizedEmail = normalizeEmail(email);
        const passwordHash = await bcrypt.hash(password, 12);

        const user = await prisma.user.create({
            data: {
                email: normalizedEmail,
                passwordHash,
                displayName: displayName?.trim() || null
            }
        });
        const refreshToken = await createRefreshToken(prisma, user.id);

        return res.status(201).json({
            user: toPublicUser(user),
            accessToken: createAccessToken(user),
            refreshToken
        });
    } catch (error) {
        if (error.code === "P2002") {
            return res.status(409).json({
                message: "Unable to register with these credentials"
            });
        }

        console.error("User registration failed:", error);
        return res.status(500).json({
            message: "Failed to register user"
        });
    }
};

const login = async (req, res) => {
    const { email, password } = req.body;
    const validationError = validateCredentials({ email, password });

    if (validationError) {
        return res.status(400).json({
            message: validationError
        });
    }

    try {
        const user = await prisma.user.findUnique({
            where: {
                email: normalizeEmail(email)
            }
        });

        const passwordMatches = user
            ? await bcrypt.compare(password, user.passwordHash)
            : false;

        if (!user || !passwordMatches) {
            return res.status(401).json({
                message: "Invalid email or password"
            });
        }
        const refreshToken = await createRefreshToken(prisma, user.id);

        return res.json({
            user: toPublicUser(user),
            accessToken: createAccessToken(user),
            refreshToken
        });
    } catch (error) {
        console.error("User login failed:", error);
        return res.status(500).json({
            message: "Failed to log in"
        });
    }
};

const getCurrentUser = (req, res) => {
    return res.json({
        user: toPublicUser(req.user)
    });
};

const updateCurrentUser = async (req, res) => {
    const { displayName } = req.body;

    if (displayName !== null && typeof displayName !== "string") {
        return res.status(400).json({
            message: "Display name must be a string or null"
        });
    }

    try {
        const user = await prisma.user.update({
            where: {
                id: req.user.id
            },
            data: {
                displayName: displayName?.trim() || null
            }
        });

        return res.json({
            user: toPublicUser(user)
        });
    } catch (error) {
        console.error("Profile update failed:", error);
        return res.status(500).json({
            message: "Failed to update profile"
        });
    }
};

const refresh = async (req, res) => {
    const { refreshToken } = req.body;

    if (typeof refreshToken !== "string" || !refreshToken) {
        return res.status(401).json({ message: "Refresh token required" });
    }

    try {
        const storedToken = await prisma.refreshToken.findUnique({
            where: { tokenHash: hashRefreshToken(refreshToken) },
            include: { user: true }
        });

        if (
            !storedToken ||
            storedToken.revokedAt ||
            storedToken.expiresAt <= new Date()
        ) {
            return res.status(401).json({ message: "Invalid refresh token" });
        }

        const replacementToken = await prisma.$transaction(async (transaction) => {
            await transaction.refreshToken.update({
                where: { id: storedToken.id },
                data: { revokedAt: new Date() }
            });

            return createRefreshToken(transaction, storedToken.userId);
        });

        return res.json({
            user: toPublicUser(storedToken.user),
            accessToken: createAccessToken(storedToken.user),
            refreshToken: replacementToken
        });
    } catch (error) {
        console.error("Token refresh failed:", error);
        return res.status(500).json({ message: "Failed to refresh session" });
    }
};

const logout = async (req, res) => {
    const { refreshToken } = req.body;

    if (typeof refreshToken !== "string" || !refreshToken) {
        return res.status(204).send();
    }

    try {
        await prisma.refreshToken.updateMany({
            where: {
                tokenHash: hashRefreshToken(refreshToken),
                revokedAt: null
            },
            data: { revokedAt: new Date() }
        });
        return res.status(204).send();
    } catch (error) {
        console.error("Logout failed:", error);
        return res.status(500).json({ message: "Failed to log out" });
    }
};

module.exports = {
    register,
    login,
    getCurrentUser,
    updateCurrentUser,
    refresh,
    logout
};

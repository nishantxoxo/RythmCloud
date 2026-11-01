const bcrypt = require("bcrypt");
const prisma = require("../config/db");
const {
    createAccessToken,
    toPublicUser
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

        return res.status(201).json({
            user: toPublicUser(user),
            accessToken: createAccessToken(user)
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

        return res.json({
            user: toPublicUser(user),
            accessToken: createAccessToken(user)
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

module.exports = {
    register,
    login,
    getCurrentUser
};

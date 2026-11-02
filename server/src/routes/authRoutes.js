const express = require("express");
const {
    register,
    login,
    getCurrentUser,
    updateCurrentUser,
    refresh,
    logout
} = require("../controllers/authController");
const { requireAuth } = require("../middlewares/auth");

const router = express.Router();

router.post("/register", register);
router.post("/login", login);
router.get("/me", requireAuth, getCurrentUser);
router.patch("/me", requireAuth, updateCurrentUser);
router.post("/refresh", refresh);
router.post("/logout", logout);

module.exports = router;

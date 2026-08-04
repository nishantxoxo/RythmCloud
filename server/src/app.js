

const express = require("express");
const cors = require("cors");

const uploadRoutes = require("./routes/uploadRoutes");
const songRoutes = require("./routes/songRoutes");
const app = express();

// Middleware
app.use(cors());
app.use(express.json());
// app.use("/uploads", express.static("uploads"));
// Make uploaded files accessible
app.use("/uploads", express.static("uploads"));

app.use("/upload", uploadRoutes);
app.use("/songs", songRoutes);



module.exports = app;
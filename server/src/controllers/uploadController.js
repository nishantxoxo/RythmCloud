const path = require("path");
const { randomUUID } = require("crypto");
const { supabase, bucketName } = require("../config/supabase");

const createStoragePath = (folder, originalName) => {
    const extension = path.extname(originalName).toLowerCase();

    return `${folder}/${randomUUID()}${extension}`;
};

const uploadFile = async (file, folder) => {
    const storagePath = createStoragePath(folder, file.originalname);

    const { error: uploadError } = await supabase.storage
        .from(bucketName)
        .upload(storagePath, file.buffer, {
            contentType: file.mimetype,
            cacheControl: "3600",
            upsert: false
        });

    if (uploadError) {
        throw uploadError;
    }

    const { data } = supabase.storage
        .from(bucketName)
        .getPublicUrl(storagePath);

    return {
        key: storagePath,
        url: data.publicUrl
    };
};

const uploadAudio = async (req, res) => {
    if (!req.file) {
        return res.status(400).json({
            message: "No audio uploaded"
        });
    }

    try {
        const result = await uploadFile(req.file, "audio");

        return res.status(201).json(result);
    } catch (error) {
        console.error("Supabase audio upload failed:", error);

        return res.status(500).json({
            message: "Failed to upload audio"
        });
    }
};

const uploadImage = async (req, res) => {
    if (!req.file) {
        return res.status(400).json({
            message: "No image uploaded"
        });
    }

    try {
        const result = await uploadFile(req.file, "images");

        return res.status(201).json(result);
    } catch (error) {
        console.error("Supabase image upload failed:", error);

        return res.status(500).json({
            message: "Failed to upload image"
        });
    }
};

module.exports = {
    uploadAudio,
    uploadImage
};
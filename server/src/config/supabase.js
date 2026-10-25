const { createClient } = require("@supabase/supabase-js");

const requiredVariables = [
    "SUPABASE_URL",
    "SUPABASE_SERVICE_ROLE_KEY",
    "SUPABASE_STORAGE_BUCKET"
];

for (const variable of requiredVariables) {
    if (!process.env[variable]) {
        throw new Error(`Missing required environment variable: ${variable}`);
    }
}

const supabase = createClient(
    process.env.SUPABASE_URL,
    process.env.SUPABASE_SERVICE_ROLE_KEY
);

const bucketName = process.env.SUPABASE_STORAGE_BUCKET;

module.exports = {
    supabase,
    bucketName
};
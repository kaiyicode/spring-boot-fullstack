import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    allowedHosts: [
      'localhost',
      'kaiyicode-api-v3-env.eba-dedtjmmh.eu-north-1.elasticbeanstalk.com',
      // Add other allowed hosts here if needed
    ],
  },
});
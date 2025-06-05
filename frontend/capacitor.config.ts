import { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
    appId: "com.campuslostfound.app",
    appName: "Campus Lost & Found",
    webDir: "dist",
    server: {
        androidScheme: "https",
    },
};

export default config;

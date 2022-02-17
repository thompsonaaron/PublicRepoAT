module.exports = {
  env: {
    browser: true,
    es2021: true,
  },
  // parser: "babel-eslint",
  extends: ["react-app", "eslint:recommended", "plugin:prettier/recommended"],
  // parserOptions: {
  //   ecmaFeatures: {
  //     jsx: true,
  //   },
  //   ecmaVersion: 2018,
  //   sourceType: "module",
  // },
  plugins: ["react", "prettier", "simple-import-sort"],
  rules: {
    "simple-import-sort/imports": "error",
  },
  settings: {
    react: {
      version: "latest", // Automatically detect the react version
    },
  },
};

const withBundleAnalyzer = require("@next/bundle-analyzer")({
	enabled: process.env.ANALYZE === "true",
	openAnalyzer: false,
});
module.exports = withBundleAnalyzer({});
// module.exports = {
// 	productionBrowserSourceMaps: true,
// };

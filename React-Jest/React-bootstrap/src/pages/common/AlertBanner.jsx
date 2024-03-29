import Alert from "react-bootstrap/Alert";
import React from "react";

const AlertBanner = ({ message, variant }) => {
	const alertMessage =
		message || "An unexpected error ocurred. Please try again later";
	const alertVariant = variant || "danger";
	return (
		<Alert variant={alertVariant} style={{ backgroundColor: "red" }}>
			{alertMessage}
		</Alert>
	);
};

export default AlertBanner;

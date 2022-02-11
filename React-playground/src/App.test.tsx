import { render, screen } from "@testing-library/react";

import App from "./App";
import React from "react";

test("renders learn react link", () => {
	render(<App />);
	const tempDescription = screen.getByText(/My brain is melting/i);
	expect(tempDescription).toBeInTheDocument();
});

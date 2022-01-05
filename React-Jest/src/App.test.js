import { render, screen } from "@testing-library/react";

import App from "./App";

// https://testing-library.com/docs/guide-which-query/
// https://www.w3.org/TR/wai-aria/#role_definitions

test("renders learn react link", () => {
	render(<App />);
	const linkElement = screen.getByRole("link", { name: /learn react/i });
	expect(linkElement).toBeInTheDocument();
});

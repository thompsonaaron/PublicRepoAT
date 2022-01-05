import { fireEvent, render, screen } from "@testing-library/react";

import App from "./App";

// https://testing-library.com/docs/guide-which-query/
// https://www.w3.org/TR/wai-aria/#role_definitions

// test("renders learn react link", () => {
// 	render(<App />);
// 	const linkElement = screen.getByRole("link", { name: /learn react/i });
// 	expect(linkElement).toBeInTheDocument();
// });

test("button has correct initial color and text", () => {
	render(<App />);
	const button = screen.getByRole("button", { name: "Change to blue" });
	expect(button).toHaveStyle({ backgroundColor: "red" });

	fireEvent.click(button);

	expect(button).toHaveStyle({ backgroundColor: "blue" });
	expect(button.textContent).toBe("Change to red");
});

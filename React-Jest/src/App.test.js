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

test("initial state of button and checkbox", () => {
	render(<App />);
	const button = screen.getByRole("button", { name: "Change to blue" });
	expect(button).toBeEnabled();

	const checkbox = screen.getByRole("checkbox");
	expect(checkbox).not.toBeChecked();
});

test("disabled state of button and checkbox", () => {
	render(<App />);
	const button = screen.getByRole("button");
	const checkbox = screen.getByRole("checkbox", { name: "disable-checkbox" });
	//const checkbox = screen.getByLabelText("Disabled?");
	fireEvent.click(checkbox);

	expect(button).toBeDisabled();
	expect(checkbox).toBeChecked();
});

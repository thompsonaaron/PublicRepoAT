import App, { replaceCamelCaseWithSpaces } from "./App";
import { fireEvent, render, screen } from "@testing-library/react";

// https://testing-library.com/docs/guide-which-query/
// https://www.w3.org/TR/wai-aria/#role_definitions

// test("renders learn react link", () => {
// 	render(<App />);
// 	const linkElement = screen.getByRole("link", { name: /learn react/i });
// 	expect(linkElement).toBeInTheDocument();
// });

test("button has correct initial color and text", () => {
	render(<App />);
	const button = screen.getByRole("button", { name: "Change to MidnightBlue" });
	expect(button).toHaveStyle({ backgroundColor: "MediumVioletRed" });

	fireEvent.click(button);

	expect(button).toHaveStyle({ backgroundColor: "MidnightBlue" });
	expect(button).toHaveTextContent("Change to MediumVioletRed");
});

test("initial state of button and checkbox", () => {
	render(<App />);
	const button = screen.getByRole("button", { name: "Change to MidnightBlue" });
	expect(button).toBeEnabled();

	const checkbox = screen.getByRole("checkbox");
	expect(checkbox).not.toBeChecked();
});

test("disabled state of button and checkbox", () => {
	render(<App />);
	const button = screen.getByRole("button");
	// name works off of label text, not the ID
	const checkbox = screen.getByRole("checkbox", { name: "Disabled?" });
	//const checkbox = screen.getByLabelText("Disabled?");
	fireEvent.click(checkbox);

	expect(button).toBeDisabled();
	expect(button).toHaveStyle({ backgroundColor: "gray" });
	expect(checkbox).toBeChecked();
});

describe("spaces before camel-case capital letters", () => {
	test("Works for no inner capital letters", () => {
		expect(replaceCamelCaseWithSpaces("red")).toBe("red");
	});

	test("Works for Medium violet red", () => {
		expect(replaceCamelCaseWithSpaces("MediumVioletRed")).toBe(
			"Medium Violet Red"
		);
	});

	test("Works for MidnightBlue", () => {
		expect(replaceCamelCaseWithSpaces("MidnightBlue")).toBe("Midnight Blue");
	});
});

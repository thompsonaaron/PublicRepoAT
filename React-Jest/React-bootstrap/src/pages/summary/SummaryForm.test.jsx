import { fireEvent, render, screen } from "@testing-library/react";

import SummaryForm from "./SummaryForm";

describe("checkbox tests", () => {
	test("checkbox is unchecked by default", () => {
		render(<SummaryForm />);
		const checkbox = screen.getByRole("checkbox", { name: "Agree to terms" });
		expect(checkbox).not.toBeChecked();
	});

	test("button disabled by default", () => {
		render(<SummaryForm />);
		const button = screen.getByRole("button", { name: "Agree" });
		expect(button).toBeDisabled();
	});

	test("checking checkbox enables button", () => {
		render(<SummaryForm />);
		const button = screen.getByRole("button", { name: "Agree" });
		const checkbox = screen.getByRole("checkbox", { name: "Agree to terms" });
		fireEvent.click(checkbox);
		expect(checkbox).toBeChecked();
		expect(button).toBeEnabled();
	});

	test("checking checkbox twice disables button again", () => {
		render(<SummaryForm />);
		const button = screen.getByRole("button", { name: /Agree/i });
		const checkbox = screen.getByRole("checkbox", { name: /Agree to terms/i });
		fireEvent.click(checkbox);
		expect(checkbox).toBeChecked();
		expect(button).toBeEnabled();
		fireEvent.click(checkbox);
		expect(checkbox).not.toBeChecked();
		expect(button).toBeDisabled();
	});
});

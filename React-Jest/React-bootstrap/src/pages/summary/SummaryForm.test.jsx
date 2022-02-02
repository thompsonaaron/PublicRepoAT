import {
	render,
	screen,
	waitForElementToBeRemoved
} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import SummaryForm from "./SummaryForm";


	test("checkbox is unchecked by default", () => {
		render(<SummaryForm />);
		const checkbox = screen.getByRole("checkbox", { name: /Agree to terms/i });
		expect(checkbox).not.toBeChecked();
	});

	test("button disabled by default", () => {
		render(<SummaryForm />);
		const button = screen.getByRole("button", { name: /Confirm Order/i });
		expect(button).toBeDisabled();
	});

	test("checking checkbox enables button", () => {
		render(<SummaryForm />);
		const button = screen.getByRole("button", { name: /Confirm Order/i });
		const checkbox = screen.getByRole("checkbox", { name: "Agree to terms" });
		userEvent.click(checkbox);
		expect(checkbox).toBeChecked();
		expect(button).toBeEnabled();
	});

	test("checking checkbox twice disables button again", () => {
		render(<SummaryForm />);
		const button = screen.getByRole("button", { name: /Confirm Order/i });
		const checkbox = screen.getByRole("checkbox", { name: /Agree to terms/i });
		userEvent.click(checkbox);
		expect(checkbox).toBeChecked();
		expect(button).toBeEnabled();
		userEvent.click(checkbox);
		expect(checkbox).not.toBeChecked();
		expect(button).toBeDisabled();
	});

	test("popover responds to hover", async () => {
		// starts hidden
		render(<SummaryForm />);
		const nullPopover = screen.queryByText(
			/no ice cream will actually be delivered/i
		);
		expect(nullPopover).not.toBeInTheDocument();

		// appears on hover
		const termsAndConditions = screen.getByText(/agree to terms/i);
		userEvent.hover(termsAndConditions);
		const popover = screen.getByText(
			/no ice cream will actually be delivered/i
		);
		expect(popover).toBeInTheDocument();

		// disappears onUnhover
		userEvent.unhover(termsAndConditions);
		const nullPopoverAgain = screen.queryByText(
			/no ice cream will actually be delivered/i
		);
		await waitForElementToBeRemoved(() =>
			screen.queryByText(/no ice cream will actually be delivered/i)
		);
	});
});

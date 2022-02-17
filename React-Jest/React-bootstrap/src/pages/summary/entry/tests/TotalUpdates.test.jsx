import { render, screen } from "@testing-library/react";

import Options from "../Options";
import userEvent from "@testing-library/user-event";

test("update scoop subtotal when scoops change", async () => {
	render(<Options optionType="scoops" />);

	// starting subTotal is 0.00
	const scoopsSubTotal = screen.getByText("Scoop total: $", { exact: false });
	expect(scoopsSubTotal).toHaveTextContent("0.00");

	// update vanilla scoops to 1 and check subtotal
	const vanillaInput = await screen.findByRole("spinbutton", {
		name: "Vanilla",
	});
	userEvent.clear(vanillaInput);
	userEvent.type(vanillaInput, "1");

	expect(scoopsSubTotal).toHaveTextContent("2.00");

	const chocolateInput = await screen.findByRole("spinbutton", {
		name: "Chocolate",
	});
	userEvent.clear(chocolateInput);
	userEvent.type(chocolateInput, "2");

	expect(scoopsSubTotal).toHaveTextContent("6.00");
});

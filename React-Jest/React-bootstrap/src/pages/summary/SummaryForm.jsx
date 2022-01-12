import { Button, Form } from "react-bootstrap";
import React, { useState } from "react";

const SummaryForm = (props) => {
	const [isChecked, setIsChecked] = useState(false);

	return (
		<Form>
			<Form.Group>
				<Form.Check
					type="checkbox"
					id="EUA-checkbox"
					checked={isChecked}
					onChange={(e) => setIsChecked(e.target.checked)}
					label={<label htmlFor="EUA-checkbox">Agree to terms</label>}
				/>
			</Form.Group>
			<Form.Group>
				<Button type="submit" disabled={!isChecked}>
					Confirm Order
				</Button>
			</Form.Group>
		</Form>
	);
};

export default SummaryForm;

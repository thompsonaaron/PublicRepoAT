import React, { useState } from "react";

import { Form } from "react-bootstrap";

const SummaryForm = (props) => {
	const [isChecked, setIsChecked] = useState(false);

	return (
		<Form>
			<Form.Group>
				<label htmlFor="EUA-checkbox">Agree to terms</label>
				<input
					type="checkbox"
					id="EUA-checkbox"
					defaultChecked={isChecked}
					aria-checked={isChecked}
					onChange={(e) => setIsChecked(e.target.checked)}
				/>
			</Form.Group>
			<Form.Group>
				<button disabled={!isChecked}>Agree</button>
			</Form.Group>
		</Form>
	);
};

export default SummaryForm;

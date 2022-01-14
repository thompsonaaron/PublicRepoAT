import { Button, Form, OverlayTrigger, Popover } from "react-bootstrap";
import React, { useState } from "react";

const SummaryForm = (props) => {
	const [isChecked, setIsChecked] = useState(false);

	const popover = (
		<Popover id="popover-basic">
			<Popover.Header as="h3">Popover right</Popover.Header>
			<Popover.Body>No ice cream will actually be delivered</Popover.Body>
		</Popover>
	);

	const label = (
		<OverlayTrigger placement="right" overlay={popover}>
			<label htmlFor="EUA-checkbox">Agree to terms</label>
		</OverlayTrigger>
	);

	return (
		<>
			<Form>
				<Form.Group>
					<Form.Check
						type="checkbox"
						id="EUA-checkbox"
						checked={isChecked}
						onChange={(e) => setIsChecked(e.target.checked)}
						label={
							<OverlayTrigger placement="right" overlay={popover}>
								<label htmlFor="EUA-checkbox">Agree to terms</label>
							</OverlayTrigger>
						}
					/>
				</Form.Group>
				<Form.Group>
					<Button type="submit" disabled={!isChecked}>
						Confirm Order
					</Button>
				</Form.Group>
			</Form>
		</>
	);
};

export default SummaryForm;

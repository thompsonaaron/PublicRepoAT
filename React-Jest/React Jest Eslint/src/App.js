import "./App.css";

import { useState } from "react";

export const replaceCamelCaseWithSpaces = (colorName) => {
	return colorName.replace(/\B([A-Z])\B/g, " $&");
};

export const blue = "MidnightBlue";
export const red = "MediumVioletRed";

function App() {
	const [buttonColor, setButtonColor] = useState(red);
	const [isEnabled, setIsEnabled] = useState(true);

	return (
		<div className="App">
			<button
				style={{ backgroundColor: isEnabled ? buttonColor : "gray" }}
				onClick={() =>
					setButtonColor((prevState) => (prevState === red ? blue : red))
				}
				disabled={!isEnabled}>
				Change to {buttonColor === red ? blue : red}
			</button>
			<label htmlFor="disable-checkbox">Disabled?</label>
			<input
				type="checkbox"
				id="disable-checkbox"
				defaultChecked={!isEnabled}
				aria-checked={!isEnabled}
				onChange={() => {
					setIsEnabled((prevState) => !prevState);
				}}
			/>
		</div>
	);
}

export default App;

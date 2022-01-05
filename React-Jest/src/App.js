import "./App.css";

import { useState } from "react";

function App() {
	const [buttonColor, setButtonColor] = useState("red");

	return (
		<div className="App">
			<button
				style={{ backgroundColor: buttonColor }}
				onClick={() =>
					setButtonColor((prevState) => (prevState === "red" ? "blue" : "red"))
				}>
				Change to {buttonColor === "red" ? "blue" : "red"}
			</button>
		</div>
	);
}

export default App;

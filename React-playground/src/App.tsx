import "./App.css";

import React from "react";
import RenderPropsApp from "./DesignPatterns/RenderProps";

function App() {
	return (
		<div className="App">
			<header>
				<div>React playground</div>
			</header>
			<body>
				<RenderPropsApp />
			</body>
		</div>
	);
}

export default App;

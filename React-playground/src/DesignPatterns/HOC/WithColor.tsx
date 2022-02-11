import React from "react";

const withColor = (WrappedComponent: React.ComponentType<any>) => {
	return class extends React.Component {
		render() {
			return <WrappedComponent color={"red"} />;
		}
	};
};

// As a HOC example, this is silly because it's not reusable.
// Should really just export withColor and save invocation for whatever reusable
// component we want to add color to (not just the app from this page)
export default withColor;

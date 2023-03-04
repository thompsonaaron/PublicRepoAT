import Head from "next/head";
import Layout from "../components/layout";

export default function Grids() {
	return (
		<Layout home>
			<Head>
				<title>Flexbox Grids</title>
			</Head>
			<main className="flex flex-col gap-1">
				<header className="flex self-center">
					Flexbox Grids with Tailwind CSS
				</header>
				<section
					id="two columns"
					className="flex gap-x-2 text-center m-2 p-2 shadow-md shadow-slate-50">
					<div className="bg-orange flex-1">1</div>
					<div className="bg-orange flex-1">2</div>
				</section>
				<section
					id="three columns"
					className="flex gap-x-2 text-center m-2 p-2 shadow-md shadow-slate-50">
					<div className="bg-orange flex basis-1/4 shrink justify-center">
						1
					</div>
					<div className="bg-orange flex grow justify-center">2</div>
					<div className="bg-orange flex basis-1/4 shrink justify-center">
						3
					</div>
				</section>
				{/* This is pretty janky; I know flexbox isn't meant for 2D grids. Note that resizing 
				the screen means the margins will take up more than the 4% remaining (3 x 32% = 96%)
				and it it no longer maintains a 3 x 3 layout
				*/}
				<section
					id="3 x 3 grid"
					className="flex gap-2 flex-wrap m-2 p-2 shadow-md shadow-slate-50">
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						1
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						2
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						3
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						4
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						5
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						6
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						7
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						8
					</div>
					<div
						className="bg-orange flex justify-center"
						style={{ flex: "0 1 32%" }}>
						9
					</div>
				</section>
			</main>
		</Layout>
	);
}

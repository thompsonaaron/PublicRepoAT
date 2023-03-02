import Image from "next/image";
import clsx from "clsx";
import utilStyles from "../styles/utils.module.css";
import { Button, useTheme } from "@material-tailwind/react";
import { useEffect, useState } from "react";

const outputOptions = ["Maybe", "I wish", "Only when tired", "Sometimes", "No"];

export default function Card({ prompt }) {
  const theme = useTheme();
  const [type, setType] = useState("success");
  const [text, setText] = useState("Let's find out");

  useEffect(() => {
    const interval = setInterval(
      () => setType((prevType) => (prevType == "success" ? "error" : "success")),
      2000
    );

    return () => clearInterval(interval);
  }, []);

  const randomizeText = () => {
    console.log("running randomizer");
    const index = Math.floor(Math.random() * outputOptions.length);
    const newValue = outputOptions[index];
    if (newValue === text) {
      randomizeText();
      return;
    }
    setText(outputOptions[index]);
  };

  console.log(`theme is ${JSON.stringify(theme?.colors)}`);
  return (
    <>
      <div className="max-w-sm w-full lg:max-w-full lg:flex lg:flex-col my-2">
        <div className="border-r border-b border-l border-gray-400 lg:border-l-1 lg:border-t lg:border-gray-400 bg-white rounded-b lg:rounded-b-none lg:rounded-r p-4 flex flex-col justify-between leading-normal">
          <div className="mb-4">
            <div
              className={clsx(
                "text-gray-900 font-bold mb-2 text-xl transition-all transition duration-1000",
                {
                  "text-orange": type === "success",
                  "text-orange-dark": type === "error",
                }
              )}
            >
              {prompt}
            </div>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex flex:row">
              <Image
                priority
                src="/images/profile.jpg"
                className="w-10 h-10 rounded-full mr-4"
                height={144}
                width={144}
                alt=""
              />

              <div className="text-sm">
                <p className="text-gray-900 leading-none">Aaron Thompson</p>
                <p className="text-gray-600">Aug 18</p>
              </div>
            </div>

            <Button variant="filled" className="justify-self-end w-4/12" onClick={randomizeText}>
              {text}
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}

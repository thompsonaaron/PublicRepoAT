import Head from "next/head";
import Layout, { siteTitle } from "../components/layout";
import utilStyles from "../styles/utils.module.css";
import Link from "next/link";

export default function Home() {
  return (
    <Layout home>
      <Head>
        <title>{siteTitle}</title>
      </Head>
      <section className={utilStyles.headingMd}>
        <p>
          I'm Aaron, a full-stack java/javascript developer and running enthusiast with a love for
          writing eloquent code that solves real-world problems.
        </p>
        <p>
          (This sample site was built as part of Vercel's{" "}
          <a href="https://nextjs.org/learn">Next.js tutorial</a>.)
        </p>
        <p>
          <Link href="/posts/first-post">See first post</Link>
        </p>
      </section>
    </Layout>
  );
}

# Tutorial

This is a starter template for [Learn Next.js](https://nextjs.org/learn).

# New Libraries

This was a chance to build a react app on Next JS with Tailwind CSS and clsx.

# Tailwind CSS

Tailwind CSS is useful because it uses utility types, which reduces the amount of CSS that
is bundled with the project. For example, compared to a stylesheet with the below:

button {
display:flex;
}

input {
display: flex;
}

Tailwind css will only ship "display: flex" once in your entire project. It is not redefined
multiple times throughout.

# CLSX

CLSX is an alternative to the classNames module (not the react "classNames" field, different thing)
as well as emotion (used in Material UI). It is helpful for dynamically applying inline styles.

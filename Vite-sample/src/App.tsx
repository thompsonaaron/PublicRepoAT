import { useState } from 'react'
import './App.css'

import { createTheme } from '@mui/material/styles'
import Button from '@mui/material/Button'
import { Autocomplete, Container, TextField } from '@mui/material'
import { themeOptions } from './Theme/ThemeOptions'
import { ThemeProvider } from '@emotion/react'

function App() {
  const theme = createTheme(themeOptions);
  const [count, setCount] = useState(0)

  return (
    <ThemeProvider theme={theme}>
      <Container maxWidth="md">
    <div >
      <h1>Vite + React</h1>
      <div>
        <Button variant="outlined" onClick={() => setCount((prevCount) => prevCount + 1)}>
          count is {count}
        </Button>
        <p>
          Edit <code>src/App.tsx</code> and save to test HMR
        </p>
      </div>
      <p className="read-the-docs">
        Click on the Vite and React logos to learn more
      </p>
      <Autocomplete options={[{label: 'test'}]} renderInput={(params) => <TextField {...params} label="Movie" />}/>
      <Button className="p-8 m-8" variant="contained">Submit</Button>
</div>
    </Container>
    </ThemeProvider>

  )
}

export default App

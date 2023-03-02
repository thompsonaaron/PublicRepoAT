interface InputProps {
    name: string;
    value: string;
    onChange: () => void;
    [k:string]: any;
}

export default function input ({name, value, onChange, ...rest}: InputProps) {
    return <input name={name} value={value} onChange={onChange} {...rest}/>
}


import React, { useRef, useState } from 'react';
import Editor from '@monaco-editor/react';

const CodeEditor = ({ code, language, onChange }) => {
  const editorRef = useRef(null);
  const [editorError, setEditorError] = useState(false);

  const languageMap = {
    'java': 'java',
    'python': 'python',
    'cpp': 'cpp',
    'c': 'c',
    'javascript': 'javascript'
  };

  const handleEditorDidMount = (editor, monaco) => {
    try {
      editorRef.current = editor;
      
      // Configure editor options
      editor.updateOptions({
        fontSize: 14,
        minimap: { enabled: false },
        scrollBeyondLastLine: false,
        wordWrap: 'on',
        lineNumbers: 'on',
        glyphMargin: false,
        folding: false,
        lineDecorationsWidth: 10,
        lineNumbersMinChars: 3,
        renderLineHighlight: 'all',
        selectOnLineNumbers: true,
        automaticLayout: true
      });

      // Set up custom key bindings
      editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.Enter, () => {
        try {
          // Trigger run code (parent component should handle this)
          const event = new CustomEvent('runCode');
          window.dispatchEvent(event);
        } catch (error) {
          console.error('Error dispatching runCode event:', error);
        }
      });
    } catch (error) {
      console.error('Error mounting editor:', error);
      setEditorError(true);
    }
  };

  const handleEditorChange = (value) => {
    try {
      if (onChange) {
        onChange(value || '');
      }
    } catch (error) {
      console.error('Error handling editor change:', error);
    }
  };

  const handleEditorError = (error) => {
    console.error('Monaco Editor error:', error);
    setEditorError(true);
  };

  // Simple fallback textarea editor
  const SimpleEditor = () => (
    <textarea
      value={code}
      onChange={(e) => {
        try {
          if (onChange) {
            onChange(e.target.value);
          }
        } catch (error) {
          console.error('Error in simple editor change:', error);
        }
      }}
      style={{
        width: '100%',
        height: '100%',
        fontFamily: 'Monaco, "Cascadia Code", "Roboto Mono", Consolas, "Courier New", monospace',
        fontSize: '14px',
        border: '1px solid #ddd',
        borderRadius: '4px',
        padding: '10px',
        resize: 'none',
        outline: 'none',
        backgroundColor: '#fff',
        color: '#333'
      }}
      placeholder={`Write your ${language} code here...`}
    />
  );

  // If there's an editor error, fall back to simple textarea
  if (editorError) {
    return (
      <div style={{ height: '100%', border: '1px solid #e0e0e0', borderRadius: '4px' }}>
        <div style={{ 
          padding: '8px', 
          background: '#fff3cd', 
          borderBottom: '1px solid #ffeaa7',
          fontSize: '0.85rem',
          color: '#856404'
        }}>
          Editor failed to load, using fallback mode
        </div>
        <div style={{ height: 'calc(100% - 40px)' }}>
          <SimpleEditor />
        </div>
      </div>
    );
  }

  return (
    <div style={{ height: '100%', border: '1px solid #e0e0e0', borderRadius: '4px' }}>
      <Editor
        height="100%"
        language={languageMap[language] || 'plaintext'}
        value={code || ''}
        onChange={handleEditorChange}
        onMount={handleEditorDidMount}
        theme="vs"
        options={{
          selectOnLineNumbers: true,
          roundedSelection: false,
          readOnly: false,
          cursorStyle: 'line',
          automaticLayout: true,
          fontSize: 14,
          fontFamily: 'Monaco, "Cascadia Code", "Roboto Mono", Consolas, "Courier New", monospace',
          minimap: { enabled: false },
          scrollBeyondLastLine: false,
          wordWrap: 'on',
          lineNumbers: 'on',
          glyphMargin: false,
          folding: true,
          lineDecorationsWidth: 10,
          lineNumbersMinChars: 3
        }}
        loading={
          <div style={{ 
            padding: '20px', 
            textAlign: 'center',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100%'
          }}>
            <div>Loading editor...</div>
          </div>
        }
        onError={handleEditorError}
      />
    </div>
  );
};

export default CodeEditor;
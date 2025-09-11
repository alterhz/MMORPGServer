using ZGame;
using UnityEngine;

public abstract class PanelBase : UIBase
{
    public PanelBase(string canvasPath, string panelName = null) : base(canvasPath, panelName)
    {
        Canvas = UIManager.Instance.GetCanvas(canvasPath);
    }

    public void Show()
    {
        Canvas panelCanvas = UIManager.Instance.GetCanvas(CanvasPath);
        panelCanvas.gameObject.SetActive(true);
    }

    public void Hide()
    {
        Canvas panelCanvas = UIManager.Instance.GetCanvas(CanvasPath);
        panelCanvas.gameObject.SetActive(false);
    }
    


}

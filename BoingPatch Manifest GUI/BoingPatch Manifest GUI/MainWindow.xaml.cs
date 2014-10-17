using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.IO;

using FolderSelect;
using System.Diagnostics;

namespace BoingPatch.GUI
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        public MainWindow()
        {
            InitializeComponent();

            FolderSelectDialog dialog = new FolderSelectDialog();
            dialog.InitialDirectory = Directory.GetCurrentDirectory();
            dialog.Title = "Select patch directory";
            if (dialog.ShowDialog())
            {
                var path = dialog.FileName;
                var name = Path.GetFileName(path);
                TreeViewItem item = new TreeViewItem();
                item.Header = name;
                item.Tag = path;
                item.FontWeight = FontWeights.Normal;
                item.Items.Add(createDummyNode());
                item.Expanded += new RoutedEventHandler(folder_Expanded);
                foldersItem.Items.Add(item);
            }
            else
            {
                this.Close();
            }
        }

        void folder_Expanded(object sender, RoutedEventArgs e)
        {
            TreeViewItem item = (TreeViewItem)sender;
            if (item.Items.Count == 1 && (item.Items[0] as TreeViewItem).Tag.ToString() == "dummy")
            {
                item.Items.Clear();
                try
                {
                    foreach (string s in Directory.GetDirectories(item.Tag.ToString()))
                    {
                        TreeViewItem subitem = new TreeViewItem();
                        subitem.Header = Path.GetFileName(s);
                        subitem.Tag = s;
                        subitem.FontWeight = FontWeights.Normal;
                        subitem.Items.Add(createDummyNode());
                        subitem.Expanded += new RoutedEventHandler(folder_Expanded);
                        item.Items.Add(subitem);
                    }
                    foreach (string s in Directory.GetFiles(item.Tag.ToString()))
                    {
                        TreeViewItem subitem = new TreeViewItem();
                        subitem.Header = Path.GetFileName(s);
                        subitem.Tag = s;
                        subitem.FontWeight = FontWeights.Normal;
                        item.Items.Add(subitem);
                    }
                }
                catch (Exception) { }
            }
        }

        private TreeViewItem createDummyNode()
        {
            TreeViewItem dummy = new TreeViewItem();
            dummy.Tag = "dummy";
            return dummy;
        }

        private void foldersItem_SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            TreeViewItem SelectedItem = foldersItem.SelectedItem as TreeViewItem;
            FileAttributes attributes = File.GetAttributes(SelectedItem.Tag.ToString());
            if ((attributes & FileAttributes.Directory) == FileAttributes.Directory)
            {
                foldersItem.ContextMenu = foldersItem.Resources["FolderContext"] as System.Windows.Controls.ContextMenu;
            }
            else
            {
                foldersItem.ContextMenu = foldersItem.Resources["FileContext"] as System.Windows.Controls.ContextMenu;

            }
        }

        private void showInExplorer(object sender, RoutedEventArgs args)
        {
            var file = (foldersItem.SelectedItem as TreeViewItem).Tag.ToString();
            Process.Start(Path.GetDirectoryName(file));
        }

        private void openFile(object sender, RoutedEventArgs args)
        {
            var file = (foldersItem.SelectedItem as TreeViewItem).Tag.ToString();
            Process.Start(file);
        }

        private void createGroup(object sender, RoutedEventArgs args)
        {

        }

        private void createSingle(object sender, RoutedEventArgs args)
        {

        }
    }
}
